# Release Playbook

## Channels

- `dev`: frequent integration builds
- `staging`: pre-beta stabilization
- `beta`: signed private beta candidate

## Criteria to promote

1. CI green (android/web/supabase)
2. QA matrix pass on required device matrix
3. Security checks pass
4. Known issues documented with severity

## Rollback

- Revert to last stable tag
- Disable risky paths via `feature_flags`
- Broadcast safe defaults through Supabase realtime