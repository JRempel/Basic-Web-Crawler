class_dir = '../web-crawler/target'
crawler_engine = 'web-crawler-jar-with-dependencies.jar'
html_dir = '../../htdocs'
	
# change working dir
import os
os.chdir(class_dir)

# invoke crawler engine
status = 'Please wait until we crawl some webpages for you. This may take some minutes...'
print(status)
os.system('java -jar ' + crawler_engine + ' CRAWL_AND_INDEX')

# revert to original directory once crawling is finished
os.chdir(html_dir)

# launch browser to use the search engine
import webbrowser
from http.server import HTTPServer, CGIHTTPRequestHandler

def run(server_class=HTTPServer, handler_class=CGIHTTPRequestHandler):
    server_address = ('', 8000)
    httpd = server_class(server_address, handler_class)
    httpd.serve_forever()

if __name__ == '__main__':
    url = 'http://localhost:8000'
    print('Server runs at', url)
    run()