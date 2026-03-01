import { createClient } from "@supabase/supabase-js";

const url = process.env.NEXT_PUBLIC_SUPABASE_URL || "http://127.0.0.1:54321";
const anonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY || "local-anon-key";
const serviceRole = process.env.SUPABASE_SERVICE_ROLE_KEY || anonKey;

export function getSupabaseClient() {
  return createClient(url, anonKey);
}

export function getSupabaseAdmin() {
  return createClient(url, serviceRole, {
    auth: { persistSession: false, autoRefreshToken: false },
  });
}