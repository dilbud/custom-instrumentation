/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.dbx;


import static java.util.Collections.singletonList;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;

import java.util.List;

/**
 * This is a demo instrumentation which hooks into servlet invocation and modifies the http
 * response.
 */
@AutoService(InstrumentationModule.class)
public final class WordCountInstrumentationModule extends InstrumentationModule {
    public WordCountInstrumentationModule() {
        super("wordcount-demo", "wordcount");
    }

    @Override
    public int order() {
        return 1;
    }
    @Override
    public List<String> getAdditionalHelperClassNames() {
        return List.of(WordCountInstrumentation.class.getName(),"io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation");
    }

//    @Override
//    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
//        return AgentElementMatchers.hasClassesNamed("org.dbx.Main");
//    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return singletonList(new WordCountInstrumentation());
    }
}