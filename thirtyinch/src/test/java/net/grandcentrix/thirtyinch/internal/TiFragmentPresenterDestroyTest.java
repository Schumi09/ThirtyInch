/*
 * Copyright (C) 2017 grandcentrix GmbH
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.grandcentrix.thirtyinch.internal;

import net.grandcentrix.thirtyinch.TiConfiguration;
import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.internal.TiFragmentDelegateBuilder.HostingActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import java.util.HashMap;

import static junit.framework.Assert.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class TiFragmentPresenterDestroyTest {

    private class PutInMapAnswer implements Answer<Void> {

        final HashMap<String, String> map = new HashMap<>();

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            final Object[] args = invocation.getArguments();
            map.put((String) args[0], (String) args[1]);
            return null;
        }
    }

    private class TestPresenter extends TiPresenter<TiView> {

        TestPresenter(TiConfiguration config) {
            super(config);
        }
    }

    private Bundle mSavedState;

    private MockSavior mSavior;

    @Test
    public void saviorFalse_retainFalse_backstackFalse_dontKeepActivitiesFalse_activityChangingConfiguration() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when the Activity is changing configurations.
        hostingActivity.setChangingConfiguration(true);
        delegate.onSaveInstanceState_afterSuper(mSavedState);
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then assert that the Presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // When the Activity gets recreated
        final HostingActivity hostingActivity2 = new HostingActivity();

        // And generates a new Fragment instance
        final TestPresenter presenter2 = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate2
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity2)
                .setSavior(mSavior)
                .setPresenter(presenter2)
                .build();

        delegate2.onCreate_afterSuper(mSavedState);
        delegate2.onCreateView_beforeSuper(mock(LayoutInflater.class), null, mSavedState);
        delegate2.onStart_afterSuper();

        // Then assert that the new Presenter does not equals the previous presenter.
        assertThat(delegate2.getPresenter()).isNotEqualTo(presenter).isEqualTo(presenter2);
    }

    @Test
    public void saviorFalse_retainFalse_backstackFalse_dontKeepActivitiesFalse_activityFinishing() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when the activity is finishing.
        hostingActivity.setFinishing(true);
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then assert that the Presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorFalse_retainFalse_backstackFalse_dontKeepActivitiesTrue_activityChangingConfiguration() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when the Activity is changing configurations.
        hostingActivity.setChangingConfiguration(true);
        delegate.onSaveInstanceState_afterSuper(mSavedState);
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then assert that the Presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // When a new Activity gets recreated
        final HostingActivity hostingActivity2 = new HostingActivity();

        // And a new Fragment instance is created by the framework.
        final TestPresenter presenter2 = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate2
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity2)
                .setSavior(mSavior)
                .setPresenter(presenter2)
                .build();

        delegate2.onCreate_afterSuper(mSavedState);
        delegate2.onCreateView_beforeSuper(mock(LayoutInflater.class), null, mSavedState);
        delegate2.onStart_afterSuper();

        // Then assert that the new Presenter does not equals the previous presenter.
        assertThat(delegate2.getPresenter()).isNotEqualTo(presenter).isEqualTo(presenter2);
    }

    @Test
    public void saviorFalse_retainFalse_backstackFalse_dontKeepActivitiesTrue_activityFinishing() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when the Activity is finishing.
        hostingActivity.setFinishing(true);
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then assert that the Presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorFalse_retainFalse_backstackTrue_dontKeepActivitiesFalse_activityChangingConfiguration() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when it is replaced by another Fragment.
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();

        // And when the Activity is changing configurations.
        hostingActivity.setChangingConfiguration(true);
        delegate.onSaveInstanceState_afterSuper(mSavedState);
        delegate.onDestroy_afterSuper();

        // Then assert that the Presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // When a new Activity gets recreated
        final HostingActivity hostingActivity2 = new HostingActivity();

        // And a new Fragment instance is created by the framework.
        final TestPresenter presenter2 = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate2
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity2)
                .setSavior(mSavior)
                .setPresenter(presenter2)
                .build();

        delegate2.onCreate_afterSuper(mSavedState);
        delegate2.onCreateView_beforeSuper(mock(LayoutInflater.class), null, mSavedState);
        delegate2.onStart_afterSuper();

        // Then assert that the new Presenter does not equals the previous presenter.
        assertThat(delegate2.getPresenter()).isNotEqualTo(presenter).isEqualTo(presenter2);
    }

    @Test
    public void saviorFalse_retainFalse_backstackTrue_dontKeepActivitiesFalse_activityFinishing() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when it is replaced by another Fragment.
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        // the hosting Activity is *not* finishing here!
        delegate.onDestroy_afterSuper();

        // Then the presenter is not destroyed
        assertThat(delegate.getPresenter().isDestroyed()).isFalse();
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // And when the Activity is finishing.
        hostingActivity.setFinishing(true);
        // TODO get finishing event somehow in Fragment
        // exactly this is the bugfix

        // Then assert that the Presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorFalse_retainFalse_backstackTrue_dontKeepActivitiesFalse_popBackstack() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when it is replaced by another Fragment.
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();

        // And when the back stack is popped.
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // Then assert that the Presenter is not destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isFalse();
        assertThat(delegate.getPresenter()).isEqualTo(presenter);
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorFalse_retainFalse_backstackTrue_dontKeepActivitiesTrue_activityChangingConfiguration() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when it is replaced by another Fragment.
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();

        // And when the Activity is changing configurations.
        hostingActivity.setChangingConfiguration(true);
        delegate.onSaveInstanceState_afterSuper(mSavedState);
        delegate.onDestroy_afterSuper();

        // Then assert that the Presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // When a new Activity gets recreated
        final HostingActivity hostingActivity2 = new HostingActivity();

        // And a new Fragment instance is created by the framework.
        final TestPresenter presenter2 = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate2
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity2)
                .setSavior(mSavior)
                .setPresenter(presenter2)
                .build();

        delegate2.onCreate_afterSuper(mSavedState);
        delegate2.onCreateView_beforeSuper(mock(LayoutInflater.class), null, mSavedState);
        delegate2.onStart_afterSuper();

        // Then assert that the new Presenter does not equals the previous presenter.
        assertThat(delegate2.getPresenter()).isNotEqualTo(presenter).isEqualTo(presenter2);
    }

    @Test
    public void saviorFalse_retainFalse_backstackTrue_dontKeepActivitiesTrue_activityFinishing() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when it is replaced by another Fragment.
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then the presenter is not destroyed
        assertThat(delegate.getPresenter().isDestroyed()).isFalse();
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // And when the Activity is finishing.
        hostingActivity.setFinishing(true);
        // TODO get finishing event somehow in Fragment

        // Then the Presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorFalse_retainFalse_backstackTrue_dontKeepActivitiesTrue_popBackstack() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when it is replaced by another Fragment.
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();

        // And when the back stack is popped.
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // Then assert that the Presenter is not destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isFalse();
        assertThat(delegate.getPresenter()).isEqualTo(presenter);
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorFalse_retainTrue_backstackFalse_dontKeepActivitiesFalse_activityChangingConfiguration() {

        final HostingActivity hostingActivity = new HostingActivity();

        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorFalse_retainTrue_backstackFalse_dontKeepActivitiesFalse_activityFinishing() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(true)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the fragment is added to the activity.
        delegate.onCreate_afterSuper(mSavedState);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when the activity is finishing.
        hostingActivity.setFinishing(true);
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then assert that the presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorFalse_retainTrue_backstackFalse_dontKeepActivitiesTrue_activityChangingConfiguration() {

        final HostingActivity hostingActivity = new HostingActivity();

        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorFalse_retainTrue_backstackFalse_dontKeepActivitiesTrue_activityFinishing() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(true)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the fragment is added to the activity.
        delegate.onCreate_afterSuper(mSavedState);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when the activity is finishing.
        hostingActivity.setFinishing(true);
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then assert that the presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorFalse_retainTrue_backstackTrue_dontKeepActivitiesFalse_activityChangingConfiguration() {

        final HostingActivity hostingActivity = new HostingActivity();

        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorFalse_retainTrue_backstackTrue_dontKeepActivitiesFalse_activityFinishing() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorFalse_retainTrue_backstackTrue_dontKeepActivitiesFalse_popBackstack() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorFalse_retainTrue_backstackTrue_dontKeepActivitiesTrue_activityChangingConfiguration() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorFalse_retainTrue_backstackTrue_dontKeepActivitiesTrue_activityFinishing() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorFalse_retainTrue_backstackTrue_dontKeepActivitiesTrue_popBackstack() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(false)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainFalse_backstackFalse_dontKeepActivitiesFalse_activityChangingConfiguration() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainFalse_backstackFalse_dontKeepActivitiesFalse_activityFinishing() {
        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the fragment is added to the activity.
        delegate.onCreate_afterSuper(mSavedState);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when the activity is finishing.
        hostingActivity.setFinishing(true);
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then assert that the presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorTrue_retainFalse_backstackFalse_dontKeepActivitiesTrue_activityChangingConfiguration() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainFalse_backstackFalse_dontKeepActivitiesTrue_activityFinishing() {
        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the fragment is added to the activity.
        delegate.onCreate_afterSuper(mSavedState);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when the activity is finishing.
        hostingActivity.setFinishing(true);
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then assert that the presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorTrue_retainFalse_backstackTrue_dontKeepActivitiesFalse_activityChangingConfiguration() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainFalse_backstackTrue_dontKeepActivitiesFalse_activityFinishing() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainFalse_backstackTrue_dontKeepActivitiesFalse_popBackstack() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainFalse_backstackTrue_dontKeepActivitiesTrue_activityChangingConfiguration() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainFalse_backstackTrue_dontKeepActivitiesTrue_activityFinishing() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainFalse_backstackTrue_dontKeepActivitiesTrue_popBackstack() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainTrue_backstackFalse_dontKeepActivitiesFalse_activityChangingConfiguration() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainTrue_backstackFalse_dontKeepActivitiesFalse_activityFinishing() {
        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the fragment is added to the activity.
        delegate.onCreate_afterSuper(mSavedState);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when the activity is finishing.
        hostingActivity.setFinishing(true);
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then assert that the presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorTrue_retainTrue_backstackFalse_dontKeepActivitiesTrue_activityChangingConfiguration() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainTrue_backstackFalse_dontKeepActivitiesTrue_activityFinishing() {
        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not use a static savior and does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        // And given a Fragment.
        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the fragment is added to the activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when the activity is finishing.
        hostingActivity.setFinishing(true);
        delegate.onStop_beforeSuper();
        // TODO onSavedInstanceState?
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then assert that the presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorTrue_retainTrue_backstackTrue_dontKeepActivitiesFalse_activityChangingConfiguration() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainTrue_backstackTrue_dontKeepActivitiesFalse_activityFinishing() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        delegate.onCreate_afterSuper(null);
        delegate.onCreateView_beforeSuper(mock(LayoutInflater.class), null, null);
        delegate.onStart_afterSuper();

        // And when it is replaced by another Fragment.
        delegate.onStop_beforeSuper();
        delegate.onDestroyView_beforeSuper();
        delegate.onDestroy_afterSuper();

        // Then the Fragment is in backstack and destroyed, but the presenter survives via savior
        assertThat(delegate.getPresenter().isDestroyed()).isFalse();
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // And when the Activity is finishing.
        hostingActivity.setFinishing(true);
        //TODO the fragment receives no event to destroy the presenter

        // Then assert that the Presenter is destroyed and not saved in the savior.
        assertThat(delegate.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    @Test
    public void saviorTrue_retainTrue_backstackTrue_dontKeepActivitiesFalse_popBackstack() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainTrue_backstackTrue_dontKeepActivitiesTrue_activityChangingConfiguration() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainTrue_backstackTrue_dontKeepActivitiesTrue_activityFinishing() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Test
    public void saviorTrue_retainTrue_backstackTrue_dontKeepActivitiesTrue_popBackstack() {
        final HostingActivity hostingActivity = new HostingActivity();
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        final TiFragmentDelegate<TiPresenter<TiView>, TiView> delegate
                = new TiFragmentDelegateBuilder()
                .setDontKeepActivitiesEnabled(true)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // TODO implement testcase
        fail();
    }

    @Before
    public void setUp() throws Exception {
        mSavior = new MockSavior();
        mSavedState = mock(Bundle.class);
        final TiFragmentPresenterDestroyTest.PutInMapAnswer putInMap = putInMap();
        doAnswer(putInMap).when(mSavedState).putString(anyString(), anyString());
    }

    @After
    public void tearDown() throws Exception {
        mSavior.clear();
        mSavedState = null;
    }

    @NonNull
    private PutInMapAnswer putInMap() {
        return new PutInMapAnswer();
    }
}
