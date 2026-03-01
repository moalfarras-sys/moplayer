# Secrets Guide

## Required secrets

- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `SUPABASE_SERVICE_ROLE_KEY`
- `WEATHER_API_KEY`
- `API_FOOTBALL_KEY`
- `POSTHOG_API_KEY`
- `POSTHOG_HOST`

## GitHub Environments

Define environments: `dev`, `staging`, `beta`.

Use OIDC federation to your secret manager and inject values at runtime.

## Android

Pass secrets through Gradle properties and map to BuildConfig in CI.

## Web

Expose only `NEXT_PUBLIC_*` to browser. Keep service role keys server-only.