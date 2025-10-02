package com.lukasabbe.optinlocatorbar;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class ModAttachmentTypes {
    public static final AttachmentType<LocatorBarOptInAttachedData> LOCATOR_BAR_OPT_IN = AttachmentRegistry.create(
            Identifier.of(OptInLocatorBar.MOD_ID, "opt_in"),
            builder -> builder
                    .initializer(() -> LocatorBarOptInAttachedData.DEFAULT)
                    .persistent(LocatorBarOptInAttachedData.CODEC)
    );
    public static void init(){}
}
