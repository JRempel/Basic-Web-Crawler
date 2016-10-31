import webbrowser
import socket

def internet(host="localhost", port=8000, timeout=3):
	try:
		socket.setdefaulttimeout(timeout)
		socket.socket(socket.AF_INET, socket.SOCK_STREAM).connect((host, port))
		return True
	except Exception as ex:
		return False

if __name__ == '__main__':
	if internet():
		url = 'http://localhost:8000'
		webbrowser.open(url)
	else:
		print ("Server not running, please run start-server.cmd first.")
		wait = input("Press ENTER to Exit.")