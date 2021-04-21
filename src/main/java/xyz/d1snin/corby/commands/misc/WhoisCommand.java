package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.Embeds;

public class WhoisCommand extends Command {

    public WhoisCommand() {
        this.aliases = "whois";
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {

        boolean isNotInCurrentGuild = false;

        if (args.length == 1) {
            e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, "Please provide a valid user ID or mention.")).queue();
            return;
        }

        User user = e.getMessage().getMentionedUsers().isEmpty() ? Corby.getAPI().getUserById(args[1]) : e.getMessage().getMentionedUsers().get(0);
        Member member = e.getMessage().getMentionedMembers().isEmpty() ? e.getGuild().getMemberById(args[1]) : e.getMessage().getMentionedMembers().get(0);

        //!
        assert user != null;
        e.getTextChannel().sendMessage(user.getAsTag()).queue();
        assert member != null;
        e.getTextChannel().sendMessage(member.getAsMention()).queue();
    }
}
