# Module Map

## Android Modules

- `:app`
  - `app/di/AppModule.kt`: Hilt providers for DB and repositories
- `:core:designsystem`
- `:core:network`
- `:core:database`
- `:core:player`
- `:feature:login`
- `:feature:home`
- `:feature:live`
- `:feature:vod`
- `:feature:search`
- `:feature:library`
- `:feature:settings`
- `:feature:supabase-sync`
- `:domain`
- `:data`

## Web

- `apps/admin-web/app` route-based control center
- `apps/admin-web/lib/supabase.ts` typed client init

## Backend

- `supabase/migrations/202603010001_initial_schema.sql`
- `supabase/functions/*` edge endpoints
