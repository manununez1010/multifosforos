// Identidad del jugador: se guarda por pestaña (sessionStorage) para poder probar con dos pestañas.
function obtenerJugadorId() {
    let id = sessionStorage.getItem('jugadorId');
    if (!id) {
        id = 'j-' + Math.random().toString(36).substring(2, 10);
        sessionStorage.setItem('jugadorId', id);
    }
    return id;
}

function mostrarMensaje(elemento, texto, tipo) {
    elemento.textContent = texto;
    elemento.className = 'mensaje ' + tipo;
}

async function unirseAPartida(partidaId, nombre) {
    const resp = await fetch('/api/partidas/' + encodeURIComponent(partidaId) + '/jugadores', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ jugadorId: obtenerJugadorId(), nombre: nombre })
    });
    const datos = await resp.json().catch(() => ({}));
    if (!resp.ok) throw new Error(datos.error || 'No se pudo unir a la partida');
    sessionStorage.setItem('nombre', nombre);
    return datos;
}
