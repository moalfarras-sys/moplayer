# ADR-0001: Monorepo + Modular Android TV Architecture

## Status
Accepted

## Context
MoPlayer needs Android TV client, admin web console, Supabase backend, and shared contracts with synchronized evolution.

## Decision
Use a single monorepo with:
- `apps/android-tv`
- `apps/admin-web`
- `supabase`
- `packages/contracts`
- `packages/design-tokens`

Android adopts modular Clean Architecture and TV-first focus navigation rules.

## Consequences
- Faster cross-surface changes with shared schemas
- Requires stronger CI partitioning and dependency discipline