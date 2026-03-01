# QA Matrix (Private Beta)

| Area | Scenario | Expected |
|---|---|---|
| Focus | Default focus on each screen | Focus starts on designated primary control |
| Focus | D-pad traversal | No dead-ends and no infinite loops |
| Navigation | Back chain | Step-wise back, double back to exit from home |
| Login | M3U URL validation | Invalid URL blocked, valid URL accepted |
| Login | Xtream validation | Credentials verified and classified |
| Player | HLS/DASH/MP4/TS | Streams play with seek and pause |
| Player | Audio/Subtitle tracks | Track selection works and persists |
| Live | Channel zap | Quick channel change with preview |
| Live | EPG current/next | Program data visible and refreshes |
| VOD | Resume movie/episode | Playback resumes from last position |
| Sync | Realtime theme update | UI updates without app restart |
| Settings | PIN lock | Adult category blocked without PIN |
| Performance | Large playlists | No ANR, acceptable memory profile |
| Device | Chromecast 4K | Pass |
| Device | NVIDIA Shield | Pass |
| Device | Mi Box | Pass |
| Device | Sony/Google TV | Pass |

## Unit Tests Added

- `M3uParserTest`: validates split to live/movies/series.
- `RecommendationEngineTest`: validates rule-based grouping recommendations.
- `ServerInputValidatorTest`: validates Xtream credentials requirements.
