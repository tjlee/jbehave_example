package com.ebay.cloud.iaas.imagemanagement.jbehave;

import com.ebay.cloud.iaas.imagemanagement.jbehave.steps.*;
import org.jbehave.core.Embeddable;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.UnderscoredCamelCaseResolver;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.ParameterConverters.DateConverter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import static org.jbehave.core.reporters.Format.*;

public class ImageManagementStory extends JUnitStory {

    private final CrossReference xref = new CrossReference();

    @Override
    public Configuration configuration() {
        Class<? extends Embeddable> embeddableClass = this.getClass();
        Properties viewResources = new Properties();
        viewResources.put("decorateNonHtml", "true");
        // Start from default ParameterConverters instance
        ParameterConverters parameterConverters = new ParameterConverters();
        // factory to allow parameter conversion and loading from external resources (used by StoryParser too)
        ExamplesTableFactory examplesTableFactory = new ExamplesTableFactory(new LocalizedKeywords(), new LoadFromClasspath(embeddableClass), parameterConverters);
        // add custom coverters
        parameterConverters.addConverters(new DateConverter(new SimpleDateFormat("yyyy-MM-dd")),
                new ParameterConverters.ExamplesTableConverter(examplesTableFactory));

        return new MostUsefulConfiguration()
                .useStoryControls(new StoryControls().doDryRun(false).doSkipScenariosAfterFailure(false))
                .useStoryLoader(new LoadFromClasspath(embeddableClass))
                .useStoryParser(new RegexStoryParser(examplesTableFactory))
                .useStoryPathResolver(new UnderscoredCamelCaseResolver())
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withCodeLocation(CodeLocations.codeLocationFromClass(embeddableClass))
                        .withDefaultFormats()
                        .withPathResolver(new FilePrintStreamFactory.ResolveToPackagedName())
                        .withViewResources(viewResources)
                        .withFormats(CONSOLE, TXT, HTML, XML)
                        .withCrossReference(xref))
                .useParameterConverters(parameterConverters)
                .useStepMonitor(xref.getStepMonitor());
    }

    @Override
    public List<CandidateSteps> candidateSteps() {
        return new InstanceStepsFactory(configuration(),
                new AdminScriptSteps(),
                new AsyncSteps(),
                new AuthorizationSteps(),
                new Converters(),
                new HWProfilesSteps(),
                new InitSteps(),
                new InstallServiceSteps(),
                new OsProfileSteps(),
                new SearchSteps()
        ).createCandidateSteps();
    }
}
