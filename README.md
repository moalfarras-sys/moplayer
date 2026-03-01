# MoPlayer Monorepo

MoPlayer is an Android TV-first IPTV operating experience with a companion admin panel and Supabase backend.

## Project Tree

- `apps/android-tv`: Kotlin + Compose TV multi-module app.
- `apps/admin-web`: Next.js 15 + TypeScript admin console.
- `supabase`: SQL migrations, RLS, and Edge Functions.
- `packages/contracts`: Shared zod contracts.
- `packages/design-tokens`: Shared UI tokens.
- `docs`: Architecture, QA matrix, release and security docs.
- `docs/integration-report.md`: Consolidated implementation and run report.

## Current Implementation Status

- Foundation: implemented scaffold + modules + CI + docs.
- Login: implemented UI for M3U/M3U File/Xtream/Smart Detect + validation pipeline.
- Player: implemented Media3 gateway + external player launcher (VLC/MX fallback).
- Live: implemented groups/channels layout and action contract.
- Movies/Series: implemented split browse and continue watching surface.
- Home widgets: implemented weather/matches/latest/recommendations shell.
- Supabase integration: implemented schema, RLS, realtime publication, edge function stubs, admin reads.
- Settings/polish: implemented baseline toggles and settings shell.

## Local Run

### Prerequisites

- JDK 17
- Android Studio Koala+ with Android SDK 34
- Node.js 22+
- pnpm 9+
- Supabase CLI

### Setup env

1. Web: copy `apps/admin-web/.env.example` to `.env.local`.
2. Android: copy `apps/android-tv/local.properties.example` to `apps/android-tv/local.properties`.

### Admin web

```bash
pnpm install
pnpm --filter admin-web dev
```

### Supabase

```bash
supabase start
supabase db reset
supabase functions serve
```

### Android TV

Open `apps/android-tv` in Android Studio, sync Gradle, then run `:app`.

## Branding Assets

Default branding assets are wired and ready:

- Android TV app resources:
  - `apps/android-tv/app/src/main/res/drawable-nodpi/moplayer_logo_primary.png`
  - `apps/android-tv/app/src/main/res/drawable-nodpi/moplayer_login_bg.png`
  - `apps/android-tv/app/src/main/res/drawable-nodpi/moplayer_tv_banner.png`
  - `apps/android-tv/app/src/main/res/drawable-nodpi/moplayer_app_icon.png`
- Admin web public assets:
  - `apps/admin-web/public/branding/moplayer-logo-primary.png`
  - `apps/admin-web/public/branding/moplayer-tv-banner.png`
  - `apps/admin-web/public/branding/moplayer-app-icon.png`

To replace with your final originals, keep the same filenames and overwrite files in place.

## CI workflows

- `.github/workflows/android-ci.yml`
- `.github/workflows/web-ci.yml`
- `.github/workflows/supabase-ci.yml`
- `.github/workflows/security.yml`

## Compliance note

No API keys are committed. IPTV source legality/compliance is deployment-owner responsibility.
