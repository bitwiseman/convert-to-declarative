package io.jenkins.plugins.todeclarative.converter.publisher;

import hudson.Extension;
import hudson.tasks.ArtifactArchiver;
import hudson.tasks.Publisher;
import io.jenkins.plugins.todeclarative.converter.api.ConverterRequest;
import io.jenkins.plugins.todeclarative.converter.api.ConverterResult;
import io.jenkins.plugins.todeclarative.converter.api.ModelASTUtils;
import io.jenkins.plugins.todeclarative.converter.api.publisher.PublisherConverter;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTBranch;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTBuildCondition;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTStage;
import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTStep;

@Extension
public class NoPublisherConverter
    implements PublisherConverter
{
    @Override
    public ModelASTStage convert( ConverterRequest request, ConverterResult result, Publisher publisher )
    {

        ModelASTBuildCondition buildCondition = ModelASTUtils.buildOrFindBuildCondition( result.getModelASTPipelineDef(), "always" );

        ModelASTBranch branch = buildCondition.getBranch();
        if(branch==null){
            branch =new ModelASTBranch( this );
            buildCondition.setBranch( branch );
        }

        ModelASTStep step = new ModelASTStep( this ){
            // need to override as per default () added at the end which doesn't work for echo..
            @Override
            public String toGroovy()
            {
                return this.getName();
            }
        };
        step.setName( "echo 'No converter for Publisher: " + publisher.getClass().getName() + "'" );
        step.setArgs( null );
        branch.getSteps().add( step );

        return null;
    }

    @Override
    public boolean canConvert( Publisher publisher )
    {
        return false;
    }
}
