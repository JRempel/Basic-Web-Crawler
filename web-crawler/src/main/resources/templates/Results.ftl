<!doctype html>

<html>
    <head>
        <title>COMP4047 Search Engine</title>
        <link rel="stylesheet" href="../style.css" />
        <link rel="stylesheet" href="../skeleton.css" />
        <link rel="stylesheet" href="../normalize.css" />
        <!-- Background(s) by Nikola Jokic: https://www.behance.net/gallery/25452205/Wallpapers-light -->
    </head>

    <body>
        <div class="fullscreen background" style="background-image:url('../images/IMG_2323.JPG');background-size: cover;" >
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
                                <#if results?has_content>
                                    <ol>
                                        <#list results as result>
                                            <li><a href="${result}">${result}</a></li>
                                        </#list>
                                    </ol>
                                <#else>
                                    <p>Sorry, no search results.</p>
                                </#if>
                        </div>
                    </section>
                </div>
            </div>
        </div>
    </body>
</html>