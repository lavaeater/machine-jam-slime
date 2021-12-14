package core

import injection.Context.inject
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class MachineJamSlime : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(inject<FirstScreen>())
        setScreen<FirstScreen>()
    }
}

