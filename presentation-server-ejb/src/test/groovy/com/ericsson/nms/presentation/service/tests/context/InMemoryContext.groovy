package com.ericsson.nms.presentation.service.tests.context

import com.ericsson.oss.itpf.sdk.context.ContextService

/**
 * In-Memory context service to be used on our tests.
 */
class InMemoryContext implements ContextService {

    private Map<String,Serializable> localContext = new HashMap<>();

    @Override
    void setContextValue(final String s, final Serializable serializable) {
        localContext.put(s, serializable)
    }

    @Override
    <T> T getContextValue(final String s) {
        return localContext.get(s)
    }

    @Override
    Map<String, Serializable> getContextData() {
        return localContext
    }
}
