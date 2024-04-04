/*
 * Copyright (c) 2015, Google Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.google.protobuf.gradle

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.Named
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

/**
 * Locates an executable that can either be found locally or downloaded from
 * repositories.  If configured multiple times, the last call wins.  If never
 * configured, the plugin should try to run the executable from system search
 * path.
 */
@CompileStatic
class ExecutableLocator implements Named {

  private final String name

  private Property<String> artifact
  private Property<String> path

  private FileCollection artifactFiles
  private Provider<String> simplifiedArtifactName

  ExecutableLocator(String name, ObjectFactory objects) {
    this.name = name
    artifact = objects.property(String)
    path = objects.property(String)
  }

  @Override
  String getName() {
    return name
  }

  /**
   * Specifies an artifact spec for downloading the executable from
   * repositories. spec format: '<groupId>:<artifactId>:<version>'
   */
  void setArtifact(String spec) {
    this.artifact.set(spec)
  }

  void setArtifact(Provider<String> spec) {
    this.artifact.set(spec)
  }

  /**
   * Specifies a local path.
   */
  void setPath(String path) {
    this.path.set(path)
  }

  void setPath(Provider<String> path) {
    this.path.set(path)
  }

  Provider<String> getArtifact() {
    return artifact
  }

  String getPath() {
    return path.getOrNull()
  }

  boolean hasArtifact() {
    this.artifact.isPresent()
  }

  boolean hasPath() {
    return path.isPresent()
  }

  @PackageScope
  FileCollection getArtifactFiles() {
    Preconditions.checkState(path.getOrNull() == null, 'Not artifact based')
    Preconditions.checkState(artifactFiles != null, 'Not yet created resolved')
    return artifactFiles
  }

  @PackageScope
  String getSimplifiedArtifactName() {
    Preconditions.checkState(path.getOrNull() == null, 'Not artifact based')
    Preconditions.checkState(simplifiedArtifactName.getOrNull() != null, 'Not yet resolved')
    return simplifiedArtifactName.getOrNull()
  }

  @PackageScope
  void resolve(FileCollection artifactFiles, Provider<String> simplifiedArtifactName) {
    this.artifactFiles = artifactFiles
    this.simplifiedArtifactName = simplifiedArtifactName
  }
}
