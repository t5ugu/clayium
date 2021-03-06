package mods.clayium.item.common;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

public class ClayiumShapedMaterial extends ItemTiered implements IItemColor {
    private final ClayiumMaterial material;
    private final ClayiumShape shape;
    private final boolean useGeneralIcon;

    public ClayiumShapedMaterial(ClayiumMaterial material, ClayiumShape shape, int tier) {
        this(material, shape, tier, false);
    }

    public ClayiumShapedMaterial(ClayiumMaterial material, ClayiumShape shape, int tier, boolean useGeneralIcon) {
        super(material, shape, tier);
        this.material = material;
        this.shape = shape;
        this.useGeneralIcon = useGeneralIcon;
    }

    @Nullable
    public String getTempFile() {
        if (!useGeneralIcon) return null;

        switch (shape) {
            case plate:
                return "plate";
            case largePlate:
                return "large_plate";
            case dust:
                return "dust";
            case ingot:
                return "ingot";
            case gem:
                switch (material) {
                    case antimatter:
                    case pureAntimatter:
                    case compressedPureAntimatter_1:
                        return "matter";
                    case compressedPureAntimatter_2:
                    case compressedPureAntimatter_3:
                        return "matter2";
                    case compressedPureAntimatter_4:
                    case compressedPureAntimatter_5:
                        return "matter3";
                    case compressedPureAntimatter_6:
                    case compressedPureAntimatter_7:
                        return "matter4";
                    case octuplePureAntimatter:
                        return "matter5";
                }
        }
        return null;
    }

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        int[] tint = material.getColors()[tintIndex];
        return new Color(tint[0], tint[1], tint[2]).getRGB();
    }

    @SubscribeEvent
    public void registerMaterialColor(ColorHandlerEvent.Item event){
        event.getItemColors().registerItemColorHandler(this, this);
    }

    @SideOnly(Side.CLIENT)
    public void registerMaterialColor(ItemColors event) {
        event.registerItemColorHandler(this, this);
    }

    public ClayiumMaterial getMaterial() {
        return material;
    }

    public boolean useGeneralIcon() {
        return useGeneralIcon;
    }
}
