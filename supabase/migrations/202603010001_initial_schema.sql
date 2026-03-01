-- MoPlayer initial schema
create extension if not exists "pgcrypto";

create table if not exists profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  display_name text,
  role text not null default 'user' check (role in ('admin','user')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists servers (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  name text not null,
  type text not null check (type in ('M3U_URL','M3U_FILE','XTREAM','SMART_DETECT')),
  base_url text not null,
  username text,
  encrypted_password text,
  external_epg_url text,
  is_default boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists themes (
  id uuid primary key default gen_random_uuid(),
  owner_user_id uuid references auth.users(id) on delete set null,
  accent_hex text not null default '#6BC7FF',
  blur_strength real not null default 24,
  glass_opacity real not null default 0.12,
  dark_mode boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists widgets (
  id uuid primary key default gen_random_uuid(),
  owner_user_id uuid references auth.users(id) on delete set null,
  weather_enabled boolean not null default true,
  matches_enabled boolean not null default true,
  leagues text[] not null default '{}',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists app_settings (
  id uuid primary key default gen_random_uuid(),
  owner_user_id uuid references auth.users(id) on delete set null,
  locale text not null default 'ar',
  parental_pin_hash text,
  external_player text,
  buffer_profile text not null default 'balanced',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists watch_history (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  content_id text not null,
  content_type text not null,
  position_ms bigint not null,
  duration_ms bigint not null,
  updated_at timestamptz not null default now()
);

create table if not exists favorites (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  content_id text not null,
  content_type text not null,
  created_at timestamptz not null default now(),
  unique(user_id, content_id)
);

create table if not exists recommendations (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  content_id text not null,
  score real not null,
  reason text,
  created_at timestamptz not null default now()
);

create table if not exists feature_flags (
  key text primary key,
  enabled boolean not null default false,
  payload jsonb,
  updated_by uuid references auth.users(id),
  updated_at timestamptz not null default now()
);

create table if not exists epg_sources (
  id uuid primary key default gen_random_uuid(),
  owner_user_id uuid references auth.users(id) on delete set null,
  name text not null,
  xmltv_url text not null,
  enabled boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists device_sessions (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  device_name text not null,
  platform text not null default 'android_tv',
  last_seen_at timestamptz not null default now()
);

create table if not exists analytics_events (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete set null,
  event_name text not null,
  event_payload jsonb,
  created_at timestamptz not null default now()
);

alter table profiles enable row level security;
alter table servers enable row level security;
alter table themes enable row level security;
alter table widgets enable row level security;
alter table app_settings enable row level security;
alter table watch_history enable row level security;
alter table favorites enable row level security;
alter table recommendations enable row level security;
alter table feature_flags enable row level security;
alter table epg_sources enable row level security;
alter table device_sessions enable row level security;
alter table analytics_events enable row level security;

create or replace function is_admin(uid uuid)
returns boolean language sql stable as $$
  select exists(select 1 from profiles p where p.id = uid and p.role = 'admin');
$$;

create policy "profiles read own" on profiles for select using (id = auth.uid());
create policy "profiles update own" on profiles for update using (id = auth.uid());

create policy "servers own" on servers for all using (user_id = auth.uid()) with check (user_id = auth.uid());
create policy "themes own or admin" on themes for all using (owner_user_id = auth.uid() or is_admin(auth.uid())) with check (owner_user_id = auth.uid() or is_admin(auth.uid()));
create policy "widgets own or admin" on widgets for all using (owner_user_id = auth.uid() or is_admin(auth.uid())) with check (owner_user_id = auth.uid() or is_admin(auth.uid()));
create policy "settings own" on app_settings for all using (owner_user_id = auth.uid()) with check (owner_user_id = auth.uid());
create policy "history own" on watch_history for all using (user_id = auth.uid()) with check (user_id = auth.uid());
create policy "favorites own" on favorites for all using (user_id = auth.uid()) with check (user_id = auth.uid());
create policy "recommendations own" on recommendations for select using (user_id = auth.uid());
create policy "epg own or admin" on epg_sources for all using (owner_user_id = auth.uid() or is_admin(auth.uid())) with check (owner_user_id = auth.uid() or is_admin(auth.uid()));
create policy "sessions own" on device_sessions for all using (user_id = auth.uid()) with check (user_id = auth.uid());
create policy "analytics own" on analytics_events for all using (user_id = auth.uid()) with check (user_id = auth.uid());

create policy "feature flags read authenticated" on feature_flags for select to authenticated using (true);
create policy "feature flags admin write" on feature_flags for all using (is_admin(auth.uid())) with check (is_admin(auth.uid()));

alter publication supabase_realtime add table themes;
alter publication supabase_realtime add table widgets;
alter publication supabase_realtime add table feature_flags;
alter publication supabase_realtime add table servers;