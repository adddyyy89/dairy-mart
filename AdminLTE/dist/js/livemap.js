let liveMap;
let liveLayer;

function ageLabel(minutes) {
  const n = Number(minutes || 0);
  if (n < 1) return 'now';
  if (n < 60) return n + ' min';
  return Math.floor(n / 60) + ' h';
}

async function loadLiveMap() {
  const status = document.getElementById('live-status');
  const list = document.getElementById('live-list');
  const el = document.getElementById('live-map');
  if (!window.L || !el) return;
  if (!liveMap) {
    liveMap = L.map('live-map').setView([20.5937, 78.9629], 5);
    L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
      attribution: 'Tiles &copy; Esri',
      maxZoom: 19,
    }).addTo(liveMap);
  }
  try {
    const rows = asList(await apiGet('/tracking/live'));
    if (liveLayer) {
      liveMap.removeLayer(liveLayer);
      liveLayer = null;
    }
    liveLayer = L.layerGroup();
    const coords = [];
    const staleMinutes = Number((typeof dmSettings === 'function' ? dmSettings().gpsStaleMinutes : 15) || 15);
    const refreshSeconds = Number((typeof dmSettings === 'function' ? dmSettings().liveMapSeconds : 30) || 30);
    rows.forEach((s) => {
      const lat = Number(s.latitude);
      const lng = Number(s.longitude);
      if (!Number.isFinite(lat) || !Number.isFinite(lng)) return;
      coords.push([lat, lng]);
      const stale = s.stale || Number(s.minutesAgo) >= staleMinutes;
      const color = stale ? '#6c757d' : '#198754';
      const marker = L.circleMarker([lat, lng], {
        radius: 9,
        color,
        fillColor: color,
        fillOpacity: 0.85,
      });
      const name = s.name || ('Salesman #' + s.userId);
      marker.bindPopup(
        `<strong>${name}</strong><br>${s.phoneNumber || ''}<br>Updated ${ageLabel(s.minutesAgo)} ago`
        + `<br><a href="./salesmandetail.html?id=${s.userId}">Open profile</a>`
      );
      marker.addTo(liveLayer);
      s._stale = stale;
    });
    liveLayer.addTo(liveMap);
    if (coords.length) {
      liveMap.fitBounds(coords, { padding: [32, 32], maxZoom: 14 });
    }
    setTimeout(() => liveMap.invalidateSize(), 250);
    status.textContent = rows.length
      ? rows.length + ' salesman location(s). Grey = last ping older than ' + staleMinutes
        + ' minutes. Refreshes every ' + refreshSeconds + 's.'
      : 'No salesman GPS pings yet. Locations appear after salesmen use the app with tracking on.';
    list.innerHTML = rows.length
      ? rows.map((s) => `
          <tr>
            <td><a href="./salesmandetail.html?id=${s.userId}">${s.name || ('#' + s.userId)}</a>
              ${s._stale ? '<span class="badge text-bg-secondary">Stale</span>' : '<span class="badge text-bg-success">Live</span>'}</td>
            <td>${ageLabel(s.minutesAgo)}</td>
          </tr>`).join('')
      : '<tr><td colspan="2" class="text-muted">No pings.</td></tr>';
  } catch (error) {
    status.textContent = error.message || 'Could not load live locations.';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  loadLiveMap();
  const seconds = Math.max(10, Number((typeof dmSettings === 'function' ? dmSettings().liveMapSeconds : 30) || 30));
  setInterval(loadLiveMap, seconds * 1000);
});
