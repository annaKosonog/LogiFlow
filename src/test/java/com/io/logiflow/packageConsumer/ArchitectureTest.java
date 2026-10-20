package com.io.logiflow.packageConsumer;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.io.logiflow.packageConsumer")
public class ArchitectureTest {

    @ArchTest
    static final com.tngtech.archunit.lang.ArchRule domain_should_not_depend_on_infrastructure =
            noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat().resideInAPackage("..infrastructure..")
                    .because("Domain layer should not depend on infrastructure layer");
}
