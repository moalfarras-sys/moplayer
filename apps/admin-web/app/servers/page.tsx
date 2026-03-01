import Link from "next/link";
import { getSupabaseAdmin } from "@/lib/supabase";

export default async function ServersPage() {
  const supabase = getSupabaseAdmin();
  const { data, error } = await supabase.from("servers").select("id,name,type,base_url,is_default").limit(50);

  return (
    <main>
      <h1 className="h1">Servers</h1>
      <p className="muted">Server registry synced from app clients and admin actions.</p>
      <p><Link href="/">? Back</Link></p>

      <section className="card">
        {error ? <p>{error.message}</p> : <pre>{JSON.stringify(data ?? [], null, 2)}</pre>}
      </section>
    </main>
  );
}