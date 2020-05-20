from pyfiglet import Figlet
figlet = Figlet(font="slant")
def application(environ, start_response):
    start_response("200 OK", [("Content-Type", "text/plain"),
                              ("Content-Encoding", "utf-8")])
    yield figlet.renderText("Hello world!").encode("utf-8")
    for k, v in environ.items():
        yield f"{k:>20} => {v}".encode("utf-8")