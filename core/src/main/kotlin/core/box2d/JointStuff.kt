package core.box2d

fun createBlob() {

    /*
    The way to do this, and the way it was implemented anyways in the actual existing Joint,
    is to have a bunch of distance joints that keep distance to a center body
    and then to it's neighbours.

    Could we make it more flat and slime-mouldy? Could we make a voronoi thing?

    Maybe - we could for sure make something that is just a bunch of blobby bodies
    that do not necessarily have a center! Let's instead have
    distance joints to at least three other bodies.

    Draw this on paper to make sense of it!
     */

}