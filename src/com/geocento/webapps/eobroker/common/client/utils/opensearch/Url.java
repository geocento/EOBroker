package com.geocento.webapps.eobroker.common.client.utils.opensearch;

import java.util.List;

/**
 * Created by thomas on 22/05/2017.
 */
public class Url {

    protected String template;
    protected String type;
    protected String rel;
    protected Integer indexOffset;
    protected Integer pageOffset;
    private List<Parameter> parameters;

    public Url() {
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public Integer getIndexOffset() {
        return indexOffset;
    }

    public void setIndexOffset(Integer indexOffset) {
        this.indexOffset = indexOffset;
    }

    public Integer getPageOffset() {
        return pageOffset;
    }

    public void setPageOffset(Integer pageOffset) {
        this.pageOffset = pageOffset;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }
}
