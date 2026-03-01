insert into feature_flags (key, enabled, payload)
values
  ('live_preview_muted', true, '{"default": true}'),
  ('pip_enabled', true, '{"default": true}'),
  ('external_player_fallback', true, '{"players": ["vlc", "mxplayer"]}')
on conflict (key) do update set enabled = excluded.enabled, payload = excluded.payload;