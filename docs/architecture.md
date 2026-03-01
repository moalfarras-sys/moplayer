# Architecture

## Monorepo

- Android TV client: `apps/android-tv`
- Admin web: `apps/admin-web`
- Supabase backend: `supabase`
- Shared contracts: `packages/contracts`
- Shared design tokens: `packages/design-tokens`

## Android Design

- Clean Architecture + MVVM
- Feature modules per domain area
- Core modules for player/network/database/design system
- TV-first focus contract with D-pad only navigation

## Data flow

1. Login/Server setup creates `ServerProfile`.
2. `PlaylistIngestor` + `XtreamClient` ingest data.
3. Persist to Room and sync metadata to Supabase.
4. Player consumes normalized stream URLs.
5. Realtime updates from Supabase mutate theme/dock/flags.

## Local persistence

- Room DAOs: servers, content, favorites, history.
- `LocalStorageRepository` keeps catalog and server profiles locally.
- Server credentials are encrypted with Android Keystore (`AES/GCM`).

## Supabase sync mode

- If `SUPABASE_URL` and `SUPABASE_ANON_KEY` are present, app uses `RestSupabaseSyncGateway`.
- Otherwise app runs with in-memory sync gateway for offline/local development.

## Realtime channels

- `theme_updates`
- `dock_updates`
- `feature_flags`
- `server_updates`
- `widget_updates`

Current scaffold maps these to table realtime updates (`themes/widgets/feature_flags/servers`) and can later add broadcast channels for finer routing.

## TV Search readiness

- `MoPlayerMediaLibraryService` is added as Media3 library service bootstrap for Google TV search/provider integration.
