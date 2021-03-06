package betterquesting.network.handlers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.events.DatabaseEvent;
import betterquesting.api.network.IPacketHandler;
import betterquesting.core.BetterQuesting;
import betterquesting.network.PacketSender;
import betterquesting.network.PacketTypeNative;
import betterquesting.storage.QuestSettings;

public class PktHandlerSettings implements IPacketHandler
{
	
	@Override
	public ResourceLocation getRegistryName()
	{
		return PacketTypeNative.SETTINGS.GetLocation();
	}
	
	@Override
	public void handleServer(NBTTagCompound tag, EntityPlayerMP sender)
	{
		if(sender == null)
		{
			return;
		}
		
		boolean isOP = sender.world.getMinecraftServer().getPlayerList().canSendCommands(sender.getGameProfile());
		
		if(!isOP)
		{
			BetterQuesting.logger.log(Level.WARN, "Player " + sender.getName() + " (UUID:" + QuestingAPI.getQuestingUUID(sender) + ") tried to edit settings without OP permissions!");
			sender.sendStatusMessage(new TextComponentString(TextFormatting.RED + "You need to be OP to edit quests!"), false);
			return; // Player is not operator. Do nothing
		}
		
		QuestSettings.INSTANCE.readPacket(tag);
		PacketSender.INSTANCE.sendToAll(QuestSettings.INSTANCE.getSyncPacket());
	}
	
	@Override
	public void handleClient(NBTTagCompound tag)
	{
		QuestSettings.INSTANCE.readPacket(tag);
		MinecraftForge.EVENT_BUS.post(new DatabaseEvent.Update());
	}
}
