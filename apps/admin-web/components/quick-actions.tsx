"use client";

import { useState } from "react";

export default function QuickActions() {
  const [flagKey, setFlagKey] = useState("live_preview_muted");
  const [flagEnabled, setFlagEnabled] = useState(true);
  const [result, setResult] = useState("");

  async function toggleFlag() {
    const res = await fetch("/api/flags/toggle", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ key: flagKey, enabled: flagEnabled }),
    });
    const payload = await res.json();
    setResult(JSON.stringify(payload));
  }

  return (
    <section className="card" style={{ marginTop: 20 }}>
      <h3 style={{ marginTop: 0 }}>Quick Actions</h3>
      <div style={{ display: "flex", gap: 10, alignItems: "center", flexWrap: "wrap" }}>
        <input value={flagKey} onChange={(e) => setFlagKey(e.target.value)} placeholder="feature flag key" />
        <label style={{ display: "flex", gap: 6, alignItems: "center" }}>
          <input type="checkbox" checked={flagEnabled} onChange={(e) => setFlagEnabled(e.target.checked)} /> Enabled
        </label>
        <button onClick={toggleFlag}>Upsert Flag</button>
      </div>
      <p className="muted">{result}</p>
    </section>
  );
}