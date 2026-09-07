package pressure;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "oceancraft")
public class PressureManager {

    private static final int SAFE_DEPTH = 20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // Only run on the server
        if (player.level().isClientSide()) {
            return;
        }

        // Don't calculate pressure outside oceans yet
        if (!isOcean(player)) {
            return;
        }

        applyPressure(player);
    }

    private static boolean isOcean(Player player) {
        // Temporary: replace with the actual ocean check
        return true;
    }

    public static void applyPressure(Player player) {

        // Sea level = Y 63
        int depth = 63 - player.blockPosition().getY();

        if (depth <= SAFE_DEPTH) {
            return;
        }

        int extraDepth = depth - SAFE_DEPTH;

        // Damage once every second
        if (player.tickCount % 20 == 0) {

            float damageAmount = 1.5f + (extraDepth / 10.0f);

            player.hurt(
                player.damageSources().source(DamageTypes.DROWN),
                damageAmount
            );
        }

        // Slowness II when more than 10 blocks past safe depth
        if (player.isCreative()) {
            return;
        }
        
        if (extraDepth > 10) {

            player.addEffect(
                new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    30,
                    1,
                    false,
                    false,
                    true
                )
            );
        }
    }
}