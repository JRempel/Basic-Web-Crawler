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

    public String createHTML(ArrayList<String> results) {
        String page = "";

        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
        try {
            Template template = configuration.getTemplate("Results.ftl");
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
