import webbrowser
from http.server import HTTPServer, CGIHTTPRequestHandler

def run(server_class=HTTPServer, handler_class=CGIHTTPRequestHandler):
    server_address = ('', 8000)
    httpd = server_class(server_address, handler_class)
    httpd.serve_forever()

if __name__ == '__main__':
    url = 'http://localhost:8000'
    print('Server runs at', url)
    webbrowser.open(url)
    run()
