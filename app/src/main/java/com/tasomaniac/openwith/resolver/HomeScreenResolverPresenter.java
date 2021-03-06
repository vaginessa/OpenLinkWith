package com.tasomaniac.openwith.resolver;

import android.content.Intent;
import android.content.res.Resources;

import com.tasomaniac.openwith.R;

import timber.log.Timber;

class HomeScreenResolverPresenter implements ResolverPresenter {

    private final Resources resources;
    private final IntentResolver intentResolver;

    HomeScreenResolverPresenter(Resources resources, IntentResolver intentResolver) {
        this.resources = resources;
        this.intentResolver = intentResolver;
    }

    @Override
    public void bind(ResolverView view, ResolverView.Navigation navigation) {
        view.setListener(new ViewListener(intentResolver, view));
        intentResolver.bind(new IntentResolverListener(view, navigation));
    }

    @Override
    public void unbind(ResolverView view) {
        view.setListener(null);
        intentResolver.unbind();
    }

    @Override
    public void release() {
        intentResolver.release();
    }

    private class IntentResolverListener implements IntentResolver.Listener {

        private final ResolverView view;
        private final ResolverView.Navigation navigation;

        IntentResolverListener(ResolverView view, ResolverView.Navigation navigation) {
            this.view = view;
            this.navigation = navigation;
        }

        @Override
        public void onIntentResolved(IntentResolver.Data data) {
            if (data.isEmpty()) {
                Timber.e("No app is found to handle url: %s", intentResolver.getSourceIntent().getDataString());
                view.toast(R.string.empty_resolver_activity);
                navigation.dismiss();
                return;
            }
            view.displayData(data);
            view.setTitle(resources.getString(R.string.add_to_homescreen));
        }

    }

    private static class ViewListener implements ResolverView.Listener {

        private final IntentResolver intentResolver;
        private final ResolverView view;

        ViewListener(IntentResolver intentResolver, ResolverView view) {
            this.intentResolver = intentResolver;
            this.view = view;
        }

        @Override
        public void onActionButtonClick(boolean always) {
            throw new IllegalStateException("Action buttons should not be visible for AddToHomeScreen");
        }

        @Override
        public void onItemClick(DisplayResolveInfo dri) {
            Intent intent = dri.intentFrom(intentResolver.getSourceIntent());
            view.displayAddToHomeScreenDialog(dri, intent);
        }

        @Override
        public void onPackagesChanged() {
            intentResolver.resolve();
        }
    }
}
