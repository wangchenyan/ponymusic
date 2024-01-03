pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://jitpack.io")
        maven("https://repo1.maven.org/maven2/")
        // banner
        maven("https://s01.oss.sonatype.org/content/groups/public")
    }
}
include(":app")
