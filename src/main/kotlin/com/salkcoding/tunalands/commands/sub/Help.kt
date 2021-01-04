package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Help : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("명령어 목록".infoFormat())
        sender.sendMessage("/ld accept: 초대를 수락합니다.".infoFormat())
        sender.sendMessage("/ld alba (name): 해당 유저를 알바로 채용하기 위한 초대를 보냅니다.".warnFormat())
        sender.sendMessage("/ld ban (name): 해당 유저를 밴합니다.".infoFormat())
        sender.sendMessage("/ld banlist: 땅에서 밴된 유저 목록을 보여줍니다.".infoFormat())
        sender.sendMessage("/ld cancel (name): 보낸 초대를 취소합니다.".infoFormat())
        sender.sendMessage("/ld delete: 자신 소유의 땅을 삭제합니다.".infoFormat())
        sender.sendMessage("/ld demote (name): 해당 유저를 강등시킵니다.".infoFormat())
        sender.sendMessage("/ld deny: 초대를 거절합니다.".infoFormat())
        sender.sendMessage("/ld hego (name): 해당 유저를 해고합니다.".warnFormat())
        sender.sendMessage("/ld help: 땅 보호 관련 명령어를 보여줍니다.".warnFormat())
        sender.sendMessage("/ld invite (name): 해당 유저를 멤버로 초대합니다.".infoFormat())
        sender.sendMessage("/ld kick (name): 땅에서 해당 유저를 쫓아냅니다.".infoFormat())
        sender.sendMessage("/ld leave: 땅을 떠납니다.".infoFormat())
        sender.sendMessage("/ld promote (name): 해당 유저를 승급시킵니다.".infoFormat())
        sender.sendMessage("/ld setspawn (main/visitor): 땅의 멤버들의 스폰과, 알바와 방문자의 스폰 지점을 설정합니다.".infoFormat())
        sender.sendMessage("/ld spawn: 땅의 스폰 지점으로 이동합니다.".infoFormat())
        sender.sendMessage("/ld unban (name): 해당 유저의 밴을 해제합니다.".infoFormat())
        sender.sendMessage("/ld visit: 방문할 수 있는 땅을 GUI로 보여줍니다.".infoFormat())
        sender.sendMessage("/ld setleader (name): 해당 유저를 땅의 소유주로 만듭니다.".infoFormat())
        if (sender.isOp) {
            sender.sendMessage("관리자 전용 디버깅 명령어 목록".infoFormat())
            sender.sendMessage("/ld debug set (target) (name) (rank): target 유저의 땅의 name 유저의 등급을 (rank)로 변경합니다.".warnFormat())
            sender.sendMessage("(rank)에 NULL 입력시 kick 처럼 해당 유저를 해당 땅에서 제명시킵니다.".warnFormat())
            sender.sendMessage("/ld debug visit (name): 해당 유저의 땅을 강제로 방문합니다.".warnFormat())
            sender.sendMessage("/ld debug player (name): 해당 유저의 소속을 조회합니다.".warnFormat())
            sender.sendMessage("/ld debug info (name): 해당 유저가 가지고 있는 땅을 보여줍니다.".warnFormat())
            sender.sendMessage("/ld debug buy: 해당 위치의 땅을 구매합니다.".warnFormat())
            sender.sendMessage("/ld debug info: 해당 위치의 땅을 판매합니다.".warnFormat())
        }
        return true
    }
}