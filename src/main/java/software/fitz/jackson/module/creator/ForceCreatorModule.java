package software.fitz.jackson.module.creator;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author Joongsoo.Park (https://github.com/joongsoo)
 * @since 2019-12-19
 */
public class ForceCreatorModule extends SimpleModule {
    @Override
    public String getModuleName() {
        return "ForceCreatorModule";
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);

        context.addBeanDeserializerModifier(ForceCreatorBeanDeserializerModifier.getInstance());
    }
}