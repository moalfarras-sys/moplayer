# Security

## Secret handling

- No API keys in source.
- Android uses local properties and CI-injected build config values.
- Web uses `.env.local` (ignored) and GitHub/Vercel env mappings.
- Supabase service role key only in secure runtime, never client-side.

## Data protection

- Credentials fields are modeled as encrypted blobs.
- RLS on all user-scoped tables.
- Admin privileges enforced through `profiles.role` + `is_admin` function.

## CI hardening

- Secret scanning workflow included.
- Dependency audits and build checks are mandatory gates.