package io.jenkins.plugins.todeclarative.converter.publishers;

import hudson.Extension;
import hudson.tasks.BuildTrigger;
import hudson.tasks.Publisher;
import io.jenkins.plugins.todeclarative.converter.ConverterRequest;
import io.jenkins.plugins.todeclarative.converter.ConverterResult;
import io.jenkins.plugins.todeclarative.converter.ModelASTUtils;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTBranch;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTBuildCondition;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTKey;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTNamedArgumentList;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTStage;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTStep;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTValue;

import java.util.HashMap;
import java.util.Map;

@Extension
public class BuildTriggerPublisherConverter
    implements PublisherConverter
{
    @Override
    public ModelASTStage convert( ConverterRequest request, ConverterResult result, Publisher publisher )
    {
        BuildTrigger buildTrigger = (BuildTrigger) publisher;
        // FIXME must depends on Threshold
        //buildTrigger.getThreshold()
        ModelASTBuildCondition buildCondition =
            ModelASTUtils.buildOrFindBuildCondition( result.getModelASTPipelineDef(), "always" );
        buildTrigger.getChildProjects().forEach( abstractProject -> {
            ModelASTBranch branch = buildCondition.getBranch();
            if(branch==null){
                branch =new ModelASTBranch( this );
                buildCondition.setBranch( branch );
            }
            // build job: 'PayloadJob'
            // TODO parameters from the main project??
            ModelASTStep build = new ModelASTStep( this );
            build.setName( "build" );
            branch.getSteps().add( build );

            Map<ModelASTKey, ModelASTValue> args = new HashMap<>();

            ModelASTKey job = new ModelASTKey( this );
            job.setKey( "job" );
            ModelASTValue urlValue = ModelASTValue.fromConstant( abstractProject.getName(), this );
            args.put( job, urlValue );

            ModelASTNamedArgumentList stepArgs = new ModelASTNamedArgumentList( null);
            stepArgs.setArguments( args );
            build.setArgs( stepArgs );


        } );
        return null;
    }

    @Override
    public boolean canConvert( Publisher publisher )
    {
        return publisher instanceof BuildTrigger;
    }
}