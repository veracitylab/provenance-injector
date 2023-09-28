package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

class AssociationCacheRegistry {

    private static volatile AssociationCache cache = null;

    private AssociationCacheRegistry() {}

    static void registerCache(AssociationCache newCache) {
        cache = newCache;
    }

    static AssociationCache getCache() {
        if (cache == null) {
            throw new IllegalStateException("No cache is registered");
        }

        return cache;
    }
}
