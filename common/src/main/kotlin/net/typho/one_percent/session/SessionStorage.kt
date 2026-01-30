package net.typho.one_percent.session

interface SessionStorage {
    fun `one_percent$getSession`(): Session?

    fun `one_percent$setSession`(session: Session?)
}