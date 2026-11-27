"""API local de la flota. Cambiar el argumento para repetir una ronda."""
import argparse
import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

ROUNDS = {
    "a": [("G1-02", "Recepción", 76), ("GO2-07", "Pasillo", 29), ("AMR-03", "Depósito", 30)],
    "b": [("G1-02", "Ascensor", 22), ("AMR-03", "Carga", 85)],
    "invalida": [("G1-02", "Ascensor", 22), ("AMR-03", "Carga", 145)],
    "vacia": [],
}

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path != "/fleet":
            self.send_error(404)
            return
        data = [dict(id=i, sector=s, battery=b) for i, s, b in ROUNDS[self.server.round_name]]
        body = json.dumps(data).encode()
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("ronda", choices=ROUNDS, nargs="?", default="a")
    server = ThreadingHTTPServer(("127.0.0.1", 8765), Handler)
    server.round_name = parser.parse_args().ronda
    server.serve_forever()
