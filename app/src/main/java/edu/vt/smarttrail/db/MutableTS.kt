package edu.vt.smarttrail.db

/**
 * Mutable version of TakenSurvey object for intermediate use before updating database with survey
 * responses
 */
class MutableTS(
    var uid: String,  // 0: MC, 1: Likert, 2: Free Response
    var datetime: String,
    var weektype: Int,
// S1
    var q100a: String,
// S2
    var q200a: String,
    var q201a: String,
    var q202a: String,
    var q203a: String,
    var q204a: String,
    var q205a: String,
    var q206a: String,
    var q207a: String,
    var q208a: String,
    var q209a: String,
// S3
    var q300a: String,
    var q301a: String,
    var q302a: String,
    var q303a: String,
    var q304a: String,
    var q305a: String,
    var q306a: String,
    var q307a: String,
    var q308a: String,
    var q309a: String,
    var q310a: String,
    var q311a: String,
    var q312a: String,
    var q313a: String,
    var q314a: String,
    var q315a: String,
    var q316a: String,
// S4
    var q400a: String,
    var q401a: String,
    var q402a: String,
    var q403a: String,
    var q404a: String,
    var q405a: String,
    var q406a: String,
    var q407a: String,
    var q408a: String,
    var q409a: String,
    var q410a: String,
    var q411a: String,
    var q412a: String,
    var q413a: String,
    var q414a: String,
    var q415a: String,
// S5
    var q500a: String,
    var q501a: String,
    var q502a: String,
    var q503a: String,
    var q504a: String,
    var q505a: String,
    var q506a: String,
    var q507a: String,
    var q508a: String,
    var q509a: String,
    var q510a: String,
    var q511a: String,
    var q512a: String,
    var q513a: String,
    var q514a: String,
    var q515a: String,
// S6
    var q600a: String,
    var q601a: String,
    var q602a: String,
    var q603a: String,
    var q604a: String,
    var q605a: String,
    var q606a: String,
    var q607a: String,
    var q608a: String,
    var q609a: String,
    var q610a: String,
    var q611a: String,
    var q612a: String,
    var q613a: String,
    var q614a: String,
    var q615a: String,
// S7
    var q700a: String,
    var q701a: String,
    var q702a: String,
    var q703a: String,
    var q704a: String,
    var q705a: String,
    var q706a: String,
    var q707a: String,
    var q708a: String,
    var q709a: String,
    var q710a: String,
// S8
    var q800a: String,
    var q801a: String,
    var q802a: String,
    var q803a: String,
    var q804a: String,
    var q805a: String,
    var q806a: String,
    var q807a: String,
    var q808a: String,
    var q809a: String,
    var q810a: String,
    var q811a: String,
    var q812a: String,
    var q813a: String,
    var q814a: String,
    var q815a: String,
    var q816a: String,
    var q817a: String,
    var q818a: String,
// S9
    var q900a: String,
    var q901a: String,
    var q902a: String,
// S10
    var q1001a: String,
    var q1002a: String,
    var q1003a: String,
    var q1004a: String,
    var q1005a: String,
    var q1006a: String,
    var q1007a: String,
    var q1008a: String,
    var q1009a: String,
    var q1010a: String,
    var q1011a: String,
    var q1012a: String,
    var q1013a: String,
    var q1014a: String
) {
    /**
     * Generate new immutable TakenSurvey object to use with database
     */
    fun createTS(): TakenSurvey {
        return TakenSurvey(0, uid, datetime, weektype, q100a, q200a, q201a, q202a, q203a, q204a, q205a, q206a, q207a, q208a, q209a, q300a, q301a, q302a, q303a, q304a, q305a, q306a, q307a, q308a, q309a, q310a, q311a, q312a, q313a, q314a, q315a, q316a, q400a, q401a, q402a, q403a, q404a, q405a, q406a, q407a, q408a, q409a, q410a, q411a, q412a, q413a, q414a, q415a, q500a, q501a, q502a, q503a, q504a, q505a, q506a, q507a, q508a, q509a, q510a, q511a, q512a, q513a, q514a, q515a, q600a, q601a, q602a, q603a, q604a, q605a, q606a, q607a, q608a, q609a, q610a, q611a, q612a, q613a, q614a, q615a, q700a, q701a, q702a, q703a, q704a, q705a, q706a, q707a, q708a, q709a, q710a, q800a, q801a, q802a, q803a, q804a, q805a, q806a, q807a, q808a, q809a, q810a, q811a, q812a, q813a, q814a, q815a, q816a, q817a, q818a, q900a, q901a, q902a, q1001a, q1002a, q1003a, q1004a, q1005a, q1006a, q1007a, q1008a, q1009a, q1010a, q1011a, q1012a, q1013a, q1014a)
    }

    /**
     * Generate new immutable TakenSurvey object with given id (primary key) to use to update
     * existing database entry
     */
    fun createTS(rowid: Long): TakenSurvey {
        return TakenSurvey(rowid, uid, datetime, weektype, q100a, q200a, q201a, q202a, q203a, q204a, q205a, q206a, q207a, q208a, q209a, q300a, q301a, q302a, q303a, q304a, q305a, q306a, q307a, q308a, q309a, q310a, q311a, q312a, q313a, q314a, q315a, q316a, q400a, q401a, q402a, q403a, q404a, q405a, q406a, q407a, q408a, q409a, q410a, q411a, q412a, q413a, q414a, q415a, q500a, q501a, q502a, q503a, q504a, q505a, q506a, q507a, q508a, q509a, q510a, q511a, q512a, q513a, q514a, q515a, q600a, q601a, q602a, q603a, q604a, q605a, q606a, q607a, q608a, q609a, q610a, q611a, q612a, q613a, q614a, q615a, q700a, q701a, q702a, q703a, q704a, q705a, q706a, q707a, q708a, q709a, q710a, q800a, q801a, q802a, q803a, q804a, q805a, q806a, q807a, q808a, q809a, q810a, q811a, q812a, q813a, q814a, q815a, q816a, q817a, q818a, q900a, q901a, q902a, q1001a, q1002a, q1003a, q1004a, q1005a, q1006a, q1007a, q1008a, q1009a, q1010a, q1011a, q1012a, q1013a, q1014a)
    }
}