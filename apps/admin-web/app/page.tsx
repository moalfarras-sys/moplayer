import { getSupabaseAdmin } from "@/lib/supabase";
import QuickActions from "@/components/quick-actions";
import Image from "next/image";
import Link from "next/link";

type KV = Record<string, unknown>;

async function readTable(name: string, limit = 8): Promise<KV[]> {
  const supabase = getSupabaseAdmin();
  const { data, error } = await supabase.from(name).select("*").limit(limit);
  if (error) return [{ error: error.message, table: name }];
  return (data ?? []) as KV[];
}

function Section({ title, rows }: { title: string; rows: KV[] }) {
  return (
    <article className="card">
      <h3 style={{ marginTop: 0 }}>{title}</h3>
      {rows.length === 0 ? (
        <p className="muted">No rows yet</p>
      ) : (
        <pre style={{ overflow: "auto", maxHeight: 240 }}>{JSON.stringify(rows, null, 2)}</pre>
      )}
    </article>
  );
}

export default async function HomePage() {
  const [servers, themes, widgets, flags, analytics, recommendations] = await Promise.all([
    readTable("servers"),
    readTable("themes"),
    readTable("widgets"),
    readTable("feature_flags"),
    readTable("analytics_events"),
    readTable("recommendations"),
  ]);

  return (
    <main>
      <div className="brandHero">
        <Image
          src="/branding/moplayer-logo-primary.png"
          alt="MoPlayer"
          width={96}
          height={96}
          style={{ borderRadius: 18 }}
        />
        <div>
          <div className="h1">MoPlayer Control Center</div>
          <p className="muted" style={{ marginTop: 0 }}>
            Private Beta admin shell: Supabase-backed configuration and realtime observability.
          </p>
        </div>
      </div>
      <p style={{ display: "flex", gap: 12 }}>
        <Link href="/servers">Servers</Link>
        <Link href="/flags">Feature Flags</Link>
      </p>

      <section className="grid" style={{ marginTop: 20 }}>
        <Section title="Servers" rows={servers} />
        <Section title="Themes" rows={themes} />
        <Section title="Widgets" rows={widgets} />
        <Section title="Feature Flags" rows={flags} />
        <Section title="Analytics Events" rows={analytics} />
        <Section title="Recommendations" rows={recommendations} />
      </section>

      <QuickActions />
    </main>
  );
}
