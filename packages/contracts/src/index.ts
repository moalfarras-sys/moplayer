import { z } from "zod";

export const serverTypeSchema = z.enum(["M3U_URL", "M3U_FILE", "XTREAM", "SMART_DETECT"]);

export const serverProfileSchema = z.object({
  id: z.string().uuid(),
  user_id: z.string().uuid(),
  name: z.string().min(2),
  type: serverTypeSchema,
  base_url: z.string().url(),
  username: z.string().optional().nullable(),
  encrypted_password: z.string().optional().nullable(),
  external_epg_url: z.string().url().optional().nullable(),
  is_default: z.boolean().default(false),
});

export const themeConfigSchema = z.object({
  id: z.string().uuid(),
  owner_user_id: z.string().uuid().nullable(),
  accent_hex: z.string(),
  blur_strength: z.number().min(0).max(80),
  glass_opacity: z.number().min(0).max(1),
  dark_mode: z.boolean(),
});

export const widgetConfigSchema = z.object({
  id: z.string().uuid(),
  owner_user_id: z.string().uuid().nullable(),
  weather_enabled: z.boolean(),
  matches_enabled: z.boolean(),
  leagues: z.array(z.string()),
});

export const featureFlagSchema = z.object({
  key: z.string().min(2),
  enabled: z.boolean(),
  payload: z.record(z.any()).nullable().optional(),
});

export type ServerProfile = z.infer<typeof serverProfileSchema>;
export type ThemeConfig = z.infer<typeof themeConfigSchema>;
export type WidgetConfig = z.infer<typeof widgetConfigSchema>;
export type FeatureFlag = z.infer<typeof featureFlagSchema>;