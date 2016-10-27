package crawler;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TemplateResponse {

    /**
     * Given a list of URLs, merge the list with the
     * Apache Freemarker template and return as a String.
     * @param results
     * @param templateName
     * @return
     */
    public String createHTML(ArrayList<String> results, String templateName) {
        String page = "";

        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");

        try {
            Template template = configuration.getTemplate(templateName);
            Map<String, Object> data = new HashMap<>();

            data.put("results", results);

            Writer out = new StringWriter();
            template.process(data, out);
            page = out.toString();

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        return page;
    }
}
