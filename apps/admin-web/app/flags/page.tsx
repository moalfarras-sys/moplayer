import Link from "next/link";
import { getSupabaseAdmin } from "@/lib/supabase";

export default async function FlagsPage() {
  const supabase = getSupabaseAdmin();
  const { data, error } = await supabase.from("feature_flags").select("*").limit(100);

  return (
    <main>
      <h1 className="h1">Feature Flags</h1>
      <p className="muted">Toggle runtime behavior for Android TV client.</p>
      <p><Link href="/">? Back</Link></p>

      <section className="card">
        {error ? <p>{error.message}</p> : <pre>{JSON.stringify(data ?? [], null, 2)}</pre>}
      </section>
    </main>
  );
}