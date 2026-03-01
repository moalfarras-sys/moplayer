pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MoPlayerAndroidTV"
include(
    ":app",
    ":core:designsystem",
    ":core:network",
    ":core:database",
    ":core:player",
    ":feature:login",
    ":feature:home",
    ":feature:live",
    ":feature:vod",
    ":feature:search",
    ":feature:library",
    ":feature:settings",
    ":feature:supabase-sync",
    ":domain",
    ":data"
)