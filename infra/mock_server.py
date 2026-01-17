import json
import re
import time

from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse


class SimpleRequestHandler(BaseHTTPRequestHandler):
    """
    Manipulador de requisições HTTP que responde a requisições GET, POST, PUT e DELETE
    de acordo com um conjunto de respostas pré-configuradas.
    """

    request_count = {}  # Contador para alternar respostas

    responses_config = {
        "/oauth2/token": {
            "POST": [
                {
                    "status_code": 200,
                    "sleep": 0,
                    "content_type": "application/json",
                    "response_body": {
                        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.........signature",
                        "token_type": "Bearer",
                        "expires_in": 3600,
                        "scope": "default"
                    }
                }
            ]
        }
    }

    def do_GET(self):
        self._handle_request("GET")

    def do_POST(self):
        self._handle_request("POST")

    def do_PUT(self):
        self._handle_request("PUT")

    def do_DELETE(self):
        self._handle_request("DELETE")

    def _handle_request(self, method):
        clean_path = urlparse(self.path).path
        matched = None

        request_body = self._read_request_body()
        print(f"\n[{method}] {clean_path}")
        print("Headers:", dict(self.headers))
        print("Body:", request_body)

        # Match de endpoint
        for path_pattern in SimpleRequestHandler.responses_config:
            if re.match(f"^{path_pattern}$", clean_path):
                matched = path_pattern
                break

        if matched and method in SimpleRequestHandler.responses_config[matched]:
            response_list = SimpleRequestHandler.responses_config[matched][method]

            # Contador por endpoint + método (mais correto)
            counter_key = f"{matched}:{method}"

            if counter_key not in SimpleRequestHandler.request_count:
                SimpleRequestHandler.request_count[counter_key] = 0

            response_config = response_list[
                SimpleRequestHandler.request_count[counter_key]
                % len(response_list)
                ]

            # Sleep configurável e seguro
            sleep_time = response_config.get("sleep", 0)
            if sleep_time and sleep_time > 0:
                time.sleep(sleep_time)

            # Tratamento correto do body (corrige string vs JSON)
            body = response_config.get("response_body")

            if isinstance(body, (dict, list)):
                response_body = json.dumps(body)
            elif isinstance(body, str):
                response_body = body
            else:
                response_body = None

            self._send_response(
                response_config["status_code"],
                response_config["content_type"],
                response_body
            )

            SimpleRequestHandler.request_count[counter_key] += 1

        else:
            self._send_error_response(
                405,
                "Method Not Allowed" if matched else "Endpoint not found"
            )

    def _send_response(self, status_code, content_type, response_body):
        """Envia uma resposta HTTP ao cliente."""
        self.send_response(status_code)
        self.send_header("Content-Type", content_type)

        if response_body:
            self.send_header("Content-Length", str(len(response_body)))

        self.end_headers()

        if response_body:
            self.wfile.write(response_body.encode("utf-8"))

    def _send_error_response(self, status_code, message):
        """Envia uma resposta de erro HTTP."""
        self.send_response(status_code)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        self.wfile.write(
            json.dumps({"error": message}).encode("utf-8")
        )

    def _read_request_body(self):
        content_length = self.headers.get('Content-Length')
        if content_length:
            length = int(content_length)
            body_bytes = self.rfile.read(length)
            try:
                return body_bytes.decode('utf-8')
            except UnicodeDecodeError:
                return body_bytes
        return None


if __name__ == "__main__":
    server_address = ('', 4040)
    httpd = HTTPServer(server_address, SimpleRequestHandler)
    print("Servidor rodando em http://localhost:4040")

    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nServidor encerrado.")
        httpd.server_close()