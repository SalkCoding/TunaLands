package com.salkcoding.tunalands.lands

enum class Rank {
    OWNER, DELEGATOR, PARTTIMEJOB, MEMBER;

    override fun toString(): String {
        return when (this) {
            OWNER -> "소유자"
            DELEGATOR -> "관리 대리인"
            PARTTIMEJOB -> "알바"
            MEMBER -> "멤버"
        }
    }
}