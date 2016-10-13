<!doctype html>

<html>
    <head>
        <title>COMP4047 Search Engine</title>
        <link rel="stylesheet" href="cgi-bin/style.css" />
        <link rel="stylesheet" href="cgi-bin/skeleton.css" />
        <link rel="stylesheet" href="cgi-bin/normalize.css" />
    </head>

    <body>
        <div class="fullscreen background" style="background-image:url('https://pixabay.com/static/uploads/photo/2016/05/26/14/04/skyline-1417229_1280.jpg');background-size: cover;" >
            <div class="overlay">
                <div class="center">
                    <section id="title" class="u-full-width" style="text-align:center;">
                        <div class="container centered">
                            <div class="twelve columns">
                                <div class="container centered">
                                    <div class="twelve columns">
                                        <h1>Search Results</h1>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                    <section id="results">
                        <div class="center-no-card">
                        <ol>
                                <#list results as result>
                                    <li><a href="${result}">${result}</a></li>
                                </#list>
                            </ol>
                        </div>
                    </section>
                </div>
            </div>
        </div>
    </body>
</html>