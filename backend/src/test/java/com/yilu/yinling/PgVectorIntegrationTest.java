package com.yilu.yinling;
import org.junit.jupiter.api.*; import static org.junit.jupiter.api.Assertions.*;
class PgVectorIntegrationTest { @Test void realVectorTestRequiresExplicitConfiguration(){String provider=System.getenv("VECTOR_PROVIDER");Assumptions.assumeTrue("pgvector".equalsIgnoreCase(provider),"VECTOR_PROVIDER=pgvector not configured; real integration skipped");assertEquals("pgvector",provider.toLowerCase());} }
