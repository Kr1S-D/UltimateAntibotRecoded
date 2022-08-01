# UltimateAntibotRecoded
INFORMATION
This is a plugin that works on both bungeecord and spigot!

Remember that being just a plugin it can't work wonders!

If you have a server with underpowered hardware or with a free host, don't expect great performances!

Also, although spigot support has been introduced, remember that bungeecord is a better choice for handling large attacks and that on spigot it could cause slowdowns!

When installing the plugin remember that if you have a bungeecord server you will have to put the plugin ONLY in the bungeecord plugin folder! If you use only spigot instead you have to put it in the spigot plugins folder then just reboot and the server!

GEYSER INFORMATION
The plugin supports the entry of players via geyser however there are some changes you need to make in the config to ensure that there are no problems!

First you have to deactivate the packet check, then you have to modify the legalname check by making it allow players with names containing the charater that geyser adds to the minecraft base name to enter, if the charater is the dot (".") then you don't have to do anything, otherwise you have to change the regex (you can ask for help in my discord server for this by opening a ticket).

PROTECTS FROM
FAST JOIN
SLOW JOIN (BYPASSABLE)
PING ATTACK
COMBINED
BAD PACKET (ONLY ON BUNGEECORD AND FORKS)

FEATURES
Fully customizable (config & messages)
VPN system for detecting slow attacks.
Hook to IPSet & IPTables for handling large attacks.
A great variety of checks to detect as many bots as possible.
An intelligent filter that activates only during attacks to avoid filtering important server errors.
An easy verification system for non-bot players during attacks.
An ID based blacklist system that will allow you to manage your blacklist more easily!
Automatic notifications when there is an attack.
COMMANDS
/uab help - displays help message
/uab toggle actionbar/title/bossbar - toggle notification
/uab stats - displays antibot stats
/uab clear blacklist/whitelist - will clean up one of the two
/uab whitelist add/remove <ip> - manage whitelist
/uab blacklist add/remove <ip> - manage blacklist
/uab firewall - displays firewall stats
/uab check <ID> - checks for a blacklisted player
/uab reload - reload messages
/uab dump - used for support assistance
PERMISSIONS
/uab help - uab.command.help
/uab toggle actionbar/title/bossbar - uab.command.toggle
/uab stats - uab.command.stats
/uab clear blacklist/whitelist - uab.command.clear
/uab whitelist add/remove <ip> - uab.command.whitelist
/uab blacklist add/remove <ip> - uab.command.blacklist
/uab firewall - uab.command.firewall
/uab check <ID> - uab.command.check
/uab reload - uab.command.reload
/uab dump - uab.command.dump
Automatic notification - uab.notification.automatic
