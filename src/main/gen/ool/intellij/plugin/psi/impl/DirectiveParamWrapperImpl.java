// This is a generated file. Not intended for manual editing.
package ool.intellij.plugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ool.intellij.plugin.psi.OxyTemplateTypes.*;
import ool.intellij.plugin.psi.*;

public class DirectiveParamWrapperImpl extends OxyTemplatePsiElementImpl implements DirectiveParamWrapper {

  public DirectiveParamWrapperImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull OxyTemplateElementVisitor visitor) {
    visitor.visitDirectiveParamWrapper(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OxyTemplateElementVisitor) accept((OxyTemplateElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public DirectiveParamFileReference getDirectiveParamFileReference() {
    return findChildByClass(DirectiveParamFileReference.class);
  }

}
