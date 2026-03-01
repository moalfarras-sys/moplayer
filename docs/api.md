# API Contracts

## Edge Functions

- `POST /functions/v1/normalize-m3u`
  - Input: raw M3U text body
  - Output: normalized `channels[]`

- `POST /functions/v1/sync-xtream`
  - Input: server credentials payload
  - Output: queued sync metadata

- `POST /functions/v1/recommend`
  - Input: `{ watched: [], catalog: [] }`
  - Output: rule-based recommendations

- `POST /functions/v1/epg-merge`
  - Input: `{ primary: [], fallback: [] }`
  - Output: merged EPG rows

## Realtime tables

- `themes`
- `widgets`
- `feature_flags`
- `servers`

## Shared Type Source

`packages/contracts/src/index.ts`