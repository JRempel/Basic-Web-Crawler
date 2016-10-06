class_dir = '../web-crawler/target'
search_engine = 'web-crawler-jar-with-dependencies.jar'

# enable cgi traceback manager
import cgitb
cgitb.enable()

# get form data
import cgi
form = cgi.FieldStorage()
if 'query' in form:
    query = form['query'].value
else:
    query = ''

# change working dir
import os
os.chdir(class_dir)

# invoke java search engine
os.system('java -jar ' + search_engine + ' ' + query)
